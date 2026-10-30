package com.yifan.newsflow.console.controller;

import com.yifan.newsflow.module.entity.OgnlDemo;
import com.yifan.newsflow.module.service.OgnlDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OgnlController {

    @Autowired
    private OgnlDemoService ognlDemoService;

    @RequestMapping("/ognl/save")
    public String save(@RequestParam(value = "ognlDemoId", required = false) Long ognlDemoId,
                       @RequestParam("name") String name,
                       @RequestParam("status") Integer status) {
        OgnlDemo demo = ognlDemoService.save(ognlDemoId, name, status);
        return "id=" + demo.getId() + "，status=" + demo.getStatus();
    }

}
