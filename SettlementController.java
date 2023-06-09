package jp.co.internous.plum.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.plum.model.domain.MstDestination;
import jp.co.internous.plum.model.mapper.MstDestinationMapper;
import jp.co.internous.plum.model.mapper.TblCartMapper;
import jp.co.internous.plum.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.plum.model.session.LoginSession;

@Controller
@RequestMapping("/plum/settlement")
public class SettlementController {
	@Autowired
	private MstDestinationMapper destinationMapper;
	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();

	@Autowired
	private TblCartMapper cartMapper;

	@Autowired
	private TblPurchaseHistoryMapper purchaseHistoryMapper;

	// データ取得
	@RequestMapping("/")
	public String settlement(Model m) {
		int userId = loginSession.getUserId();

		List<MstDestination> destinations = destinationMapper.findByUserId(userId);

		m.addAttribute("destinations", destinations);
		m.addAttribute("loginSession", loginSession);

		return "settlement";
	}

	// 購入履歴テーブルに挿入、カートテーブルから削除、購入履歴画面に遷移
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		int id = Integer.parseInt(map.get("destinationId"));

		int userId = loginSession.getUserId();

		int insertCount = purchaseHistoryMapper.insert(id, userId);

		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = cartMapper.deleteByUserId(userId);
		}

		return deleteCount == insertCount;
	}
}
