package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.mgt.dao.monitor.MOffsetRecordDao;
import com.unistack.tamboo.mgt.model.monitor.MOffsetRecord;
import com.unistack.tamboo.mgt.model.monitor.MOffsetS;
import com.unistack.tamboo.mgt.model.monitor.Throughput;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/6/6
 */
@Component
public class WorkoutPeakOffsetJob extends BaseService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MOffsetRecordDao mOffsetRecordDao;

	@Autowired
	private MqService mqService;

	public static Logger log = LoggerFactory.getLogger(WorkoutPeakOffsetJob.class);

	private static ThreadLocal<SimpleDateFormat> sdf = ThreadLocal
			.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	/**
	 * 计算出峰值
	 *
	 * @param
	 * @param
	 */
	public void workoutPeakNum() {
		List<Long> topicList = MonitorUtils.listAllTopicId();

		for (Long topicId : topicList) {
			try {
				MOffsetRecord mOffsetRecord = new MOffsetRecord();
				Throughput throughput = setMaxOffsetRate(topicId);

				long curOffset = maxOffset(topicId);
				if (curOffset == 0L) {
					continue;
				}
				long preOffset = throughput.getTotalOffset();
				long curMaxMsgRate = throughput.getMessageRate();
				mOffsetRecord.setTopicId(topicId);
				mOffsetRecord.setOffsetTotal(throughput.getTotalOffset());
				mOffsetRecord.setPeakNum(throughput.getTotalOffset());
				mOffsetRecord.setCreateTime(new Date(System.currentTimeMillis()));
				mOffsetRecord.setPeakTime(new Date(throughput.getTimestamp()));
				mOffsetRecord.setMessageRate(curMaxMsgRate);
				mqService.setBytesValue((KAFKA_RECORD + topicId).getBytes(), JsonUtils.toByteArray(mOffsetRecord),
						3600);

				if (curOffset >= preOffset) {
					mOffsetRecordDao.save(mOffsetRecord);
				}
//               当前速率大于上一次的速率则更新数据
				if (curMaxMsgRate > throughput.getMessageRate()) {
					mOffsetRecordDao.updateMessageRate(topicId, new Date(), curMaxMsgRate);
				}
//                log.info("save to mOffsetRecord value:{}", mOffsetRecord.toString());
			} catch (Exception e) {
				log.error("Failed to workout peak num", e);
			}
		}
	}

	/**
	 * 存储之前的offset到redis.
	 *
	 * @param
	 */
	private Throughput setMaxOffsetRate(long topicId) {
		Throughput throughput = new Throughput();
		throughput.setMessageRate(maxMsgRate(topicId));
		throughput.setTotalOffset(maxOffset(topicId));
		throughput.setTimestamp(getPeakTime(topicId).getTime());
		return throughput;
	}

	private long lastMaxMsgRate(long topicId) {
		return getThroughput(topicId).getMessageRate();
	}

	/**
	 * 获取吞吐量
	 *
	 * @return
	 */
	private MOffsetRecord getThroughput(Long topicId) {
		byte[] throughput = mqService.getBytesValue((KAFKA_RECORD + topicId).getBytes());
		if (Objects.isNull(throughput)) {
			MOffsetRecord mOffsetRecord = MonitorUtils.getOffsetMonitorRecord(topicId);
			if (Objects.isNull(mOffsetRecord)) {
				return makeUpZero(topicId);
			}
			return mOffsetRecord;
		} else {
			return JSON.parseObject(throughput, MOffsetRecord.class);
		}
	}

	private MOffsetRecord makeUpZero(Long topicId) {
		MOffsetRecord mOffsetRecord = new MOffsetRecord();
		mOffsetRecord.setCreateTime(new Date(System.currentTimeMillis()));
		mOffsetRecord.setMessageRate(0L);
		mOffsetRecord.setOffsetTotal(0L);
		mOffsetRecord.setPeakNum(0L);
		mOffsetRecord.setTopicId(topicId);
		return mOffsetRecord;
	}

	/**
	 * 获取最大峰值
	 *
	 * @param topicId
	 * @return
	 */
	private long maxOffset(Long topicId) {

		String todayTime = sdf.get().format(getZeroTime());

		String sql = "select s.offset_add from m_offset_s s where s.topic_id = " + topicId + " and s.accept_time > '"
				+ todayTime + "' group by s.offset_add order by s.offset_add desc limit 0,1";
		try {
			MOffsetS s = jdbcTemplate.queryForObject(sql, new RowMapper<MOffsetS>() {
				@Override
				public MOffsetS mapRow(ResultSet rs, int rowNum) throws SQLException {
					MOffsetS mOffsetS = new MOffsetS();
					long offset = rs.getLong("offset_add");
					mOffsetS.setOffsetAdd(offset);
					return mOffsetS;
				}
			});
			return s.getOffsetAdd();
		} catch (EmptyResultDataAccessException e) {
			return 0L;
		}
	}

	/**
	 * 获取最大的消息率
	 *
	 * @param topicId
	 * @return
	 */
	private MOffsetS maxMOffsetS(Long topicId) {
		String todayTime = sdf.get().format(getZeroTime());
		MOffsetS mOffsetS = new MOffsetS();
		String sql = "select s.message_rate,s.accept_time from m_offset_s s where s.topic_id = " + topicId
				+ " and s.accept_time > '" + todayTime
				+ "' group by s.message_rate order by s.message_rate desc limit 0,1";

		try {
			jdbcTemplate.queryForObject(sql, new RowMapper<MOffsetS>() {
				@Override
				public MOffsetS mapRow(ResultSet rs, int rowNum) throws SQLException {
					long msgRate = rs.getLong("message_rate");

					Timestamp time = rs.getTimestamp("accept_time");
					mOffsetS.setMessageRate(msgRate);
					mOffsetS.setAcceptTime(time);
					return mOffsetS;
				}
			});
			return mOffsetS;
		} catch (EmptyResultDataAccessException e) {
			mOffsetS.setMessageRate(0L);
			mOffsetS.setAcceptTime(new Timestamp(System.currentTimeMillis()));
			return mOffsetS;
		}
	}

	private long maxMsgRate(Long topicId) {
		return maxMOffsetS(topicId).getMessageRate();
	}

	private Date getPeakTime(Long topicId) {
		return maxMOffsetS(topicId).getAcceptTime();
	}

}
