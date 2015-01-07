package com.fp.mysite.controller.bbs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.fp.mysite.bbs.dao.BbsDao;
import com.fp.mysite.bbs.dao.BbsVo;
import com.fp.mysite.bbs.dao.FileBean;
import com.fp.mysite.handler.ModuleAndView;
import com.mysql.jdbc.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Controller(value = "viewController")
public class ViewContoller {

	private static final Logger logger = LoggerFactory
			.getLogger(ViewContoller.class);
	// MEssageSource 선언
	@Resource(name = "messageSource")
	private MessageSource messageSource;

	// Resource 어노테이션을 이용하여 BbsDao 선언.
	@Resource(name = "bbsDao")
	private BbsDao bbsDao;

	// 게시판 목록
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String dispBbsList(HttpServletRequest request, Model model) {

		logger.info("display view BBS list");

		// 검색을 위한
		String sch_type = request.getParameter("sch_type");
		String sch_value = request.getParameter("sch_value");
		Map mapSearch = new HashMap();
		mapSearch.put("sch_type", sch_type);
		mapSearch.put("sch_value", sch_value);

		// 검색 값을 뷰에 넘겨줌
		model.addAttribute("mapSearch", mapSearch);
		List<BbsVo> list = this.bbsDao.getSelect(mapSearch);
		model.addAttribute("list", list);

		logger.info("totcal count" + list.size());
		return "bbs/list";
	}

	// 게시판 상세보기
	// PathVariable 어노테이션을 이용하여 RESTful 방식 적용
	// bbs/1 -> id = 1; id = 게시물 번호로 인식함.
	@RequestMapping("/{idx}")
	public String dispBbsView(HttpServletResponse response,
			HttpServletRequest request, @PathVariable int idx, Model model) {
		logger.info("display view BBS view idx = {}", idx);

		// 저장된 쿠키 불러오기
		Cookie cookies[] = request.getCookies();
		Map mapCookie = new HashMap();
		if (request.getCookies() != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie obj = cookies[i];
				mapCookie.put(obj.getName(), obj.getValue());
			}
		}

		// 저장된 쿠키중에 read_count 만 불러오기
		String cookie_read_count = (String) mapCookie.get("read_count");
		// 저장될 새로운 쿠키값 생성
		String new_cookie_read_count = "|" + idx;

		// 저장된 쿠키에 새로운 쿠키값이 존재하는 지 검사
		if (StringUtils.indexOfIgnoreCase(cookie_read_count,
				new_cookie_read_count) == -1) {
			// 없을 경우 쿠키 생성
			Cookie cookie = new Cookie("read_count", cookie_read_count
					+ new_cookie_read_count);
			// cookie.setMaxAge(1000); // 초단위
			response.addCookie(cookie);

			// 조회수 업데이트
			this.bbsDao.updateReadCount(idx);
		}

		BbsVo object = this.bbsDao.getSelectOne(idx);

		model.addAttribute("object", object);
		return "bbs/view";
	}

	// 게시판 쓰기
	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public String dispBbsWrite(
			@RequestParam(value = "idx", defaultValue = "0") int idx,
			Model model) {
		if (idx > 0) {
			BbsVo object = this.bbsDao.getSelectOne(idx);
			model.addAttribute("object", object);
		}

		return "bbs/write";
	}

	@RequestMapping(value = "/write_ok", method = RequestMethod.POST)
	public View procBbsWrite(@ModelAttribute("bbsVo") @Valid BbsVo bbsVo,
			BindingResult result, Model model/*
											 * RedirectAttributes
											 * redirectAttributes
											 */) {

		XmlResult xml = new XmlResult();

		if (result.hasErrors()) {

			/*
			 * String message = null; List<FieldError> errors =
			 * result.getFieldErrors(); for(FieldError error : errors){ message
			 * = error.getObjectName() + "-" + error.getDefaultMessage(); }
			 */
			// String message = (String)
			// result.getFieldErrors().iterator().next().getDefaultMessage();
			String message = messageSource.getMessage(result.getFieldError(),
					Locale.ENGLISH);
			xml.setMessage(message);
			xml.setError(true);
			model.addAttribute("xmlData", xml);
			return xmlView;
		}

		Integer idx = bbsVo.getIdx();

		try {
			if (idx == null || idx == 0) {
				this.bbsDao.insert(bbsVo);
				xml.setMessage("추가되었습니다.");
				xml.setError(false);
				// redirectAttributes.addFlashAttribute("message", "추가되었습니다.");
				// return "redirect:/";
			} else {
				this.bbsDao.update(bbsVo);
				xml.setMessage("수정되었습니다.");
				xml.setError(false);
				// redirectAttributes.addFlashAttribute("message", "수정되었습니다.");
				// return "redirect:/write?idx=" + idx;
			}

		} catch (Exception e) {
			xml.setMessage(e.getMessage());
			xml.setError(true);
		}
		model.addAttribute("xmlData", xml);
		return xmlView;
	}

	@Resource
	@Qualifier(value = "jsonView")
	private View jsonView;

	@Resource
	@Qualifier("xmlView")
	private View xmlView;

	@Resource(name = "xstreamMarshaller")
	private XStreamMarshaller xStreamMarshaller;

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public View procBbsDelete(
			@RequestParam(value = "idx", required = true) int idx, Model model) {

		// XStream xst = xStreamMarshaller.getXStream();
		// xst.alias("result", XmlResult.class);

		XmlResult xml = new XmlResult();
		try {
			this.bbsDao.delete(idx);
			xml.setMessage("삭제되었습니다.");
			xml.setError(false);

		} catch (Exception e) {
			xml.setMessage(e.toString());
			xml.setError(true);
		}

		model.addAttribute("xmlData", xml);
		return xmlView;
	}

	@RequestMapping(value = "/file_upload", method = RequestMethod.POST)
	public String procFileUpload(FileBean fileBean, HttpServletRequest request,
			Model model) {
		HttpSession session = request.getSession();
		// String root_path = session.getServletContext().getRealPath("/"); //
		// 웹서비스 root 경로
		String root_path = "C:/BoardPlatform/workspace/MySite/src/main/webapp/"; // 웹서비스
																					// root
																					// 경로
		String attach_path = "resources/files/attach/";

		MultipartFile upload = fileBean.getUpload();
		String filename = "";
		String CKEditorFuncNum = "";
		if (upload != null) {
			filename = upload.getOriginalFilename();
			fileBean.setFilename(filename);
			CKEditorFuncNum = fileBean.getCKEditorFuncNum();
			try {
				File file = new File(root_path + attach_path + filename);
				logger.info(root_path + attach_path + filename);
				upload.transferTo(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String file_path = "/" + attach_path + filename;
		model.addAttribute("file_path", "localhost" + file_path);
		model.addAttribute("CKEditorFuncNum", CKEditorFuncNum);

		return "bbs/fileupload";
	}

	/**프라마커 템플릿 예제
	 * Test servlet-context.xml FreeMarkerViewResolver 주석 해제
	 * @return
	 */
	@RequestMapping(value = "/freemarker")
	public ModelAndView dispDcoumentList() {
		String module = "document";
		ModuleAndView views = new ModuleAndView(module);
		views.setTemplate("document.list.html");
		
		return views.render(true);

	}
}

// 맨하단에 XmlResult 클래스 추가
// 아래 [영역 1-1] 보다 단순하게 작업하려면 어노테이션을 이용하면 된다. @XStreamAlias("result") 상단에 추가하면
// [영역 1-2] 와 같은 결과를 얻을 수 있으며 [영역 1-1] 작업은 생략해도 된다.
@XStreamAlias("result")
class XmlResult {

	private String message;
	private boolean error = false;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean getError() {
		return this.error;
	}
}
