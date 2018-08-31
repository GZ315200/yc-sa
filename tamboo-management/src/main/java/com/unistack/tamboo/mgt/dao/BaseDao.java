package com.unistack.tamboo.mgt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@NoRepositoryBean
public interface BaseDao<T, I extends Serializable> extends JpaRepository<T, I> {

}
