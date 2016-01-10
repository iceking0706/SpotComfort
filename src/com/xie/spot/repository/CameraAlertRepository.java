package com.xie.spot.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.CameraAlert;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface CameraAlertRepository extends JpaRepository<CameraAlert, Long>{
	/**
	 * 判断某个相机是否存在未处理的某种类型的报警
	 * 如果存在，则无需再次产生新的报警了
	 * @param sn
	 * @param type
	 * @return
	 */
	public boolean meHasUnProcessedCmrAlert(String sn,Integer type);
	
	public PageData<CameraAlert> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 找出需要发送邮件的那些报警记录
	 * @param count
	 * @return
	 */
	public List<CameraAlert> meNeedToSendMail(int count);
}
