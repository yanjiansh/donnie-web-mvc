package com.example.servingwebcontent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
    private static final String BASH_PATH = "/Users/yanjiansh/Documents/test/";

    @GetMapping("/fileUpload.html")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "fileUpload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        LOGGER.info(String.format("Upload fileName:{%s}", fileName));
        File destFile = new File(BASH_PATH + fileName);

        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("Upload file:{%s} success!", fileName);
    }

    @PostMapping("/multiUpload")
    @ResponseBody
    public String multiUpload(HttpServletRequest request) {
        List<MultipartFile> multipartFiles = ((MultipartHttpServletRequest)request).getFiles("file");
        StringBuilder sb = new StringBuilder();
        for (MultipartFile file : multipartFiles) {
            String fileName = file.getOriginalFilename();
            LOGGER.info(String.format("Upload fileName:{%s}", fileName));
            File destFile = new File(BASH_PATH + fileName);
            try {
                file.transferTo(destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(String.format("Upload file:{%s} success!", fileName));
        }
        return sb.toString();
    }

    @RequestMapping("/fileDownload")
    @ResponseBody
    public String fileDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "README.md";
        String downloadFilePath = BASH_PATH + fileName;
        File downloadFile = new File(downloadFilePath);
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" +  fileName);
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(downloadFile);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0 , i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info(String.format("Success to download file:{%s}", downloadFilePath));
        return "SUCCESS";
    }

}
