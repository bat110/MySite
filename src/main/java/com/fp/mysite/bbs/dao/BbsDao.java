package com.fp.mysite.bbs.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "bbsDao")
@Transactional(readOnly=true)
public class BbsDao {
    @Resource(name = "bbsMapper")
    private BbsMapper bbsMapper;

    public List<BbsVo> getSelect() {
        return this.bbsMapper.select();
    }

    public BbsVo getSelectOne(int idx) {
        return this.bbsMapper.selectOne(idx);
    }

    @Resource(name = "transactionManager")
    protected DataSourceTransactionManager txManager;

    @Transactional
    public void insert(BbsVo bbsVo) {
         this.bbsMapper.insert(bbsVo);
    	
    	/*
    	DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    	def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    	TransactionStatus txStatus= txManager.getTransaction(def);
    	
    	try {
    	     this.bbsMapper.insert(bbsVo);
    	     int a = 1 / 0;
    	     txManager.commit(txStatus);
    	} catch(Exception e) {
    	     txManager.rollback(txStatus);
    	     throw new RuntimeException("insert 강제로 오류를 발생시킴");
    	}
    	*/
    }

    @Transactional
    public void update(BbsVo bbsVo) {
         this.bbsMapper.update(bbsVo);
    }
    
    @Transactional
    public void delete(int idx) {
         this.bbsMapper.delete(idx);
         throw new RuntimeException("강제로 오류를 발생시킴");
    }
    
    public List<BbsVo> getSelect(Map map) {
        return this.bbsMapper.select(map);
    }
    @Transactional
	public void updateReadCount(int idx) {
		this.bbsMapper.updateReadCount(idx);
	}
}