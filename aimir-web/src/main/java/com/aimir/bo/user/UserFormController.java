package com.aimir.bo.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.SimpleFormController;

import com.aimir.model.user.User;
import com.aimir.service.user.UserManager;

@Controller
@RequestMapping("/userform.*")
//public class UserFormController extends SimpleFormController {
public class UserFormController{
    private final Log log = LogFactory.getLog(UserFormController.class);
    @Autowired
    UserManager userManager;
    
    @Autowired(required = false)
//	@Qualifier("beanValidator")
	Validator validator;
    
    
    @RequestMapping(method=RequestMethod.POST)
    public String onSubmit(@ModelAttribute("User")User user,
          BindingResult result, ModelMap model) {
    	log.debug("entering 'onSubmit' method...");       
    	log.debug("modelMap : " + model.size());
        validator.validate(user, result);
        if(result.hasErrors())
        	return "userForm";
        
    	return "redirect:users.do";
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public String initializeForm(@RequestParam(value="id", required=false) String rId, ModelMap model){    	
    	
        if ((rId != null) && rId.length() > 0) {
            //return userManager.get(rId);
        	model.put("user", userManager.get(rId));
        } else {
            //return new User();
        	model.put("user", new User());
        }
        
        return "userForm";
    }
    
   
    		
    
//--------------------------- deprecated API -----------------------------
//    public UserFormController() {
//        setCommandName("user");
//        setCommandClass(User.class);
//        setFormView("userForm");
//        setSuccessView("redirect:users.do");
//        if (validator != null)
//            setValidator(validator);
//    }    

//    public ModelAndView processFormSubmission(HttpServletRequest request,
//                                              HttpServletResponse response,
//                                              Object command,
//                                              BindException errors)
//            throws Exception {
//        if (request.getParameter("cancel") != null) {
//            return new ModelAndView(getSuccessView());
//        }
//
//        return super.processFormSubmission(request, response, command, errors);
//    }

    /**
     * Set up a custom property editor for converting Longs
     */
//    protected void initBinder(HttpServletRequest request,
//                              ServletRequestDataBinder binder) {
//        // convert java.util.Date
//        SimpleDateFormat dateFormat = new SimpleDateFormat(getText("date.format"));
//        dateFormat.setLenient(false);
//        binder.registerCustomEditor(Date.class, null, 
//                new CustomDateEditor(dateFormat, true));
//        
//        // convert java.lang.Long
//        binder.registerCustomEditor(Long.class, null,
//                new CustomNumberEditor(Long.class, null, true));
//    }

//    public ModelAndView onSubmit(HttpServletRequest request,
//                                 HttpServletResponse response, Object command,
//                                 BindException errors)
//            throws Exception {
//        log.debug("entering 'onSubmit' method...");
//
//        User user = (User) command;
//
//        if (request.getParameter("delete") != null) {
//            userManager.delete(user.getId().toString());
//            request.getSession().setAttribute("message", 
//                    getText("user.deleted", user.getFullName()));
//        } else {
//            userManager.add(user);
//            request.getSession().setAttribute("message",
//                    getText("user.saved", user.getFullName()));
//        }
//
//        return new ModelAndView(getSuccessView());
//    }

//    protected Object formBackingObject(HttpServletRequest request)
//            throws ServletException {
//        String userId = request.getParameter("id");
//
//        if ((userId != null) && userId.length() > 0) {
//            return userManager.get(userId);
//        } else {
//            return new User();
//        }
//    }
    
    /**
     * Convenience method for getting a i18n key's value.  Calling
     * getMessageSourceAccessor() is used because the RequestContext variable
     * is not set in unit tests b/c there's no DispatchServlet Request.
     *
     * @param msgKey the i18n key to lookup
     * @return the message for the key
     */
//    public String getText(String msgKey) {
//        return getMessageSourceAccessor().getMessage(msgKey);
//    }

    /**
     * Convenient method for getting a i18n key's value with a single
     * string argument.
     *
     * @param msgKey the i18n key to lookup
     * @param arg arguments to substitute into key's value
     * @return the message for the key
     */
//    public String getText(String msgKey, String arg) {
//        return getText(msgKey, new Object[] { arg });
//    }

    /**
     * Convenience method for getting a i18n key's value with arguments.
     *
     * @param msgKey the i18n key to lookup
     * @param args arguments to substitute into key's value
     * @return the message for the key
     */
//    public String getText(String msgKey, Object[] args) {
//        return getMessageSourceAccessor().getMessage(msgKey, args);
//    }
}
