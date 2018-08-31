package com.unistack.tamboo.mgt.controller.alert;

import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.enums.EWebSocketTopic;
import com.unistack.tamboo.mgt.controller.BaseController;
import com.unistack.tamboo.mgt.dao.alert.AlertRecordDao;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.unistack.tamboo.mgt.common.ServerResponse.*;

/**
 * AlertRecord控制层
 */
@RestController
@RequestMapping("/alertRecords")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlertRecordController extends BaseController {


    @Autowired
    private AlertRecordDao alertRecordDao;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 获取指定条数的AlertRecord,默认最新100条
     *
     * @param size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ServerResponse list(@RequestParam Integer size) {
        Integer num = (size == null) ? 100 : size;
        Sort sort = new Sort(Direction.DESC, "timestamp");
        Pageable pageable = new PageRequest(0, num, sort);
        return createBySuccess(this.alertRecordDao.getByResolved(false, pageable).iterator());
    }

    /**
     * 更新AlertRecord
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_ADMIN"})
    public ServerResponse updateRecord(@RequestBody List<Long> ids) {
        List<AlertRecord> records = this.alertRecordDao.findByIds(ids);
        for (AlertRecord alertRecord : records) {
            alertRecord.setAlertCount(0L);
            alertRecord.setResolved(true);
        }
        List<AlertRecord> alertRecords = this.alertRecordDao.save(records);
        for (AlertRecord record : alertRecords) {
            template.convertAndSend(EWebSocketTopic.TAMBOO_MANAGER_ALERT.getName(), record);
        }
        return createBySuccess();
    }

}
