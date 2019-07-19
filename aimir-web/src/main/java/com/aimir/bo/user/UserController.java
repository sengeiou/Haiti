package com.aimir.bo.user;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aimir.service.user.UserManager;

//@Controller
public class UserController {
//    @Autowired
    UserManager userManager;

    @RequestMapping("/users.*")
    public String execute(ModelMap model) {
        model.addAttribute("userList", userManager.gets());
        return "userList";
    }

    @RequestMapping("/admin/login.*")
    public String AdminLogin() {
        return "/admin/login";
    }

    @RequestMapping("/customer/login.*")
    public String CustomerLogin() {
        return "/customer/login";
    }
}
