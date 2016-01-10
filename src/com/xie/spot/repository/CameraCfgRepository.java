package com.xie.spot.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xie.spot.entity.CameraCfg;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface CameraCfgRepository extends JpaRepository<CameraCfg, Long>{
	/**
	 * 通过sn得到摄像头数据
	 * 
	 * @param sn
	 * @return
	 */
	public CameraCfg findBySn(String sn);
	
	/**
	 * 自定义的查询
	 * @param mapCon
	 * @param pageParam
	 * @return
	 */
	public PageData<CameraCfg> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 判断某个sn是否已经存在了
	 * @param sn
	 * @param exceptId
	 * @return
	 */
	public boolean meIsSnExist(String sn,Long exceptId);
	
	/**
	 * 判断是否可以删除
	 * @param cfg
	 * @return
	 */
	public boolean canDelete(CameraCfg cfg);
	
	/**
	 * 所有的设备都变成脱机
	 */
	@Modifying
	@Query(value="update CameraCfg t set t.online=0,t.ip='',t.port=0")
	public void meSetAllCameraOffline();
	
	/**
	 * 找到所有联机的摄像机，当然是必须启用的相机
	 * @return
	 */
	@Query(value="from CameraCfg t where t.inUse=1 and t.online=1")
	public List<CameraCfg> meFindOnlineCameras();
	
	/**
	 * 找到全部在用的相机
	 * @return
	 */
	@Query(value="from CameraCfg t where t.inUse=1")
	public List<CameraCfg> meFindInUseCameras();
	
	/**
	 * 参数应用到全部的相机
	 * @param inoutInterval
	 * @param tkpInterval
	 * @param tkpHourSt
	 * @param tkpHourEd
	 */
	@Modifying
	@Query(value="update CameraCfg t set t.inoutInterval=?1,t.tkpInterval=?2,t.tkpHourSt=?3,t.tkpHourEd=?4,t.offlineTimeout=?5")
	public void updateParamsToAll(Integer inoutInterval, Integer tkpInterval, Integer tkpHourSt, Integer tkpHourEd,Integer offlineTimeout);
}
