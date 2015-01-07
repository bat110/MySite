package com.fp.mysite.handler;

import org.springframework.web.servlet.ModelAndView;

public class ModuleAndView  extends ModelAndView {
	 /**
     * 레이아웃을 메인 템플릿으로 사용하여 body 속에 템플릿을 include 하는 방식으로 페이지를 완성한다.
     */
    // 레이아웃
    private String layout = "layouts/default/layout.html";
    // 모듈
    private String module = null;
    // 스킨
    private String skin = "tpl";
    // 템플릿
    private String template = null;

    public ModuleAndView() {
    }

    public ModuleAndView(String layout, String module, String skin, String template) {
         this.layout = layout;
         this.module = module;
         this.skin = skin;
         this.template = template;
    }

    public ModuleAndView(String module, String template) {
         this.module = module;
         this.template = template;
    }

    public ModuleAndView(String module) {
         this.module = module;
    }

    public void setModule(String module) {
         this.module = module;
    }

    public void setSkin(String skin) {
         this.skin = skin;
    }

    public void setTemplate(String template) {
         this.template = template;
    }

    public ModelAndView render() {
         return this.render(true);
    }
    public ModelAndView render(boolean show_layout) {
         String tpl = "/modules/" + this.module + "/" + this.skin + "/" + this.template;

         if (!show_layout) {
              this.setViewName(tpl);
         } else {
              this.addObject("TPL", tpl);
              this.setViewName(this.layout);
         }
         return this;
    }

}
