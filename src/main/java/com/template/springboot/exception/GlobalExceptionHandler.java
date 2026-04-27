package com.template.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/4xx");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("status", 404);
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/5xx");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("status", 500);
        mav.addObject("message", "An unexpected error occurred.");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }
}
