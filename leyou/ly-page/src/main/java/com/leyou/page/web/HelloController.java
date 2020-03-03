package com.leyou.page.web;

import com.leyou.page.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("msg","hello thymelraf");
        return "hello";
    }

    @GetMapping("show2")
    public String show2(Model model){
        User user=new User();
        user.setAge(21);
        user.setName("张三");
        user.setFriend(new User("李四",22,null));
        model.addAttribute("user",user);
        return "hello";
    }

   /* @GetMapping("show3")
    public String show3(Model model){
        model.addAttribute("today", new Date());
        return "hello";
    }*/
}
