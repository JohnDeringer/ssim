package com.sri.ssim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author <a href="mailto:pam.griffith@sri.com">Pam Griffith</a>
 *         Date: 1/2/13
 */
@Controller
public class WebappController {

    @RequestMapping(value = "/")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("index");
        // mav.addObject("msg", "a message");
        return mav;
	}

	@RequestMapping(value = "/index.html")
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView("index");
        // mav.addObject("msg", "a message");
        return mav;
	}

    /**
     * Upload videos
     */
    @RequestMapping("/video")
    public ModelAndView video(@RequestParam(value="name", defaultValue = "") String name) {
		ModelAndView mav = new ModelAndView("video");
        mav.addObject("video", name);
		return mav;
    }

    /**
     * Success message for video upload
     */
    @RequestMapping("/video-success")
    public ModelAndView videoSuccess() {
        ModelAndView mav = new ModelAndView("upload-success");
        mav.addObject("type", "video");
        return mav;
    }

    /**
     * Upload interviews
     */
    @RequestMapping("/interview")
    public ModelAndView interview(@RequestParam(value="name", defaultValue = "") String name) {
		ModelAndView mav = new ModelAndView("interview");
        mav.addObject("interview", name);
		return mav;
    }

    /**
     * Success message for interview upload
     */
    @RequestMapping("/interview-success")
    public ModelAndView interviewSuccess() {
        ModelAndView mav = new ModelAndView("upload-success");
        mav.addObject("type", "interview");
        return mav;
    }

    /**
     * Search
     */
    @RequestMapping("/search")
    public ModelAndView search() {
		ModelAndView mav = new ModelAndView("search");
		return mav;
    }

    /**
     * Documentation
     */
    @RequestMapping("/documentation")
    public ModelAndView documentation() {
		ModelAndView mav = new ModelAndView("documentation");
		return mav;
    }
    @RequestMapping("/documentation-upload")
    public ModelAndView documentationUpload() {
		ModelAndView mav = new ModelAndView("documentation-upload");
		return mav;
    }

}
