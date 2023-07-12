package com.devon.contractmanagementsystem.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import com.devon.contractmanagementsystem.service.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devon.contractmanagementsystem.service.FileStorageService;
import com.devon.contractmanagementsystem.message.ResponseFile;
import com.devon.contractmanagementsystem.message.ResponseMessage;
import com.devon.contractmanagementsystem.model.FileDB;
import com.devon.contractmanagementsystem.model.User;
import com.devon.contractmanagementsystem.model.UserFileMapping;
import com.devon.contractmanagementsystem.repository.UserFileMappingRepository;
import com.devon.contractmanagementsystem.repository.UserRepository;

@RestController
@CrossOrigin
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFileMappingRepository userFileMappingRepository;

    @Autowired
    private EmailSender emailSender;

    private String generateCSVContent(List<ResponseFile> files) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Sr. no.,File name,Effective Date,Expiration Date,Status\n");
        int count = 1;
        for (ResponseFile file : files) {
            String status = file.getExpirationDate() != null && file.getExpirationDate().compareTo(new Date()) >= 0 ? "Active" : "Inactive";
            LocalDateTime effectiveDateTime = LocalDateTime.ofInstant(file.getEffectiveDate().toInstant(), ZoneId.systemDefault());
            LocalDateTime expirationDateTime = LocalDateTime.ofInstant(file.getExpirationDate().toInstant(),ZoneId.systemDefault());
            ZonedDateTime effectiveZonedDateTime = effectiveDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime expirationZonedDateTime = expirationDateTime.atZone(ZoneId.systemDefault());
            csvContent.append(count).append(",")
                    .append(file.getName()).append(",")
                    .append(file.getEffectiveDate() != null ? effectiveZonedDateTime.format(dateFormatter) : "").append(",")
                    .append(file.getExpirationDate() != null ? expirationZonedDateTime.format(dateFormatter) : "").append(",")
                    .append(status).append("\n");
            count++;
        }
        return csvContent.toString();
    }

    @PostMapping("/upload/{userId}")
    public ResponseEntity<ResponseMessage> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("effectiveDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date effectiveDate,
            @RequestParam("expirationDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date expirationDate,
            @PathVariable("userId") int userId
    ) {
        String message = "";
        try {
//            System.out.println("userId: "+userId);
            User user = userRepository.findById(userId);
            if(user != null)
            {
                FileDB existingFile = storageService.getFileByName(file.getOriginalFilename());
                if(existingFile != null) {
                    UserFileMapping userFileMapping = userFileMappingRepository.findByUserIdAndContentId(userId,existingFile.getId());
                    if (userFileMapping != null) {
                        existingFile.setData(file.getBytes());
                        existingFile.setExpirationDate(expirationDate);
                        existingFile.setEffectiveDate(effectiveDate);
                        storageService.saveFile(existingFile);
                        message = "File updated successfully: " + file.getOriginalFilename();
                    } else {
                        message = "You don't have permission to update the file "+ file.getOriginalFilename();
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage(message));
                    }
                }else{
                        FileDB fileDB = storageService.store(file, user.getId(), effectiveDate, expirationDate);
                        UserFileMapping mapping = new UserFileMapping(user.getId(), fileDB.getId());
                        userFileMappingRepository.save(mapping);
                        message = "Uploaded the file successfully: ";
                    }
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }
            else{
                throw new IllegalArgumentException("Invalid user ID: "+userId);
            }

        } catch (Exception e) {
            message = "Could not upload/update the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/file/{userId}")
    public ResponseEntity<List<ResponseFile>> getUserFiles(@PathVariable int userId) {
        List<ResponseFile> files = storageService.getUserFiles(userId).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length,
                    dbFile.getEffectiveDate(),
                    dbFile.getExpirationDate());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("file/csv/{userId}")
    public ResponseEntity<byte[]> generateCSV(@PathVariable int userId) {
        List<ResponseFile> files = storageService.getUserFiles(userId).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length,
                    dbFile.getEffectiveDate(),
                    dbFile.getExpirationDate());
        }).collect(Collectors.toList());

        String csvContent = generateCSVContent(files);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentLength(csvBytes.length);
        headers.setContentDispositionFormData("attachment", "files.csv");

        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileDB fileDB = storageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void sendExpirationAlertEmails() {
         List<FileDB> expiringFiles = storageService.getExpiringFiles(15); // Get files expiring in 15 days
        String info = "";
        for (FileDB file : expiringFiles) {
            User user = getUserForFile(file);

            if (user != null && user.getEmail() != null) {
                String subject = "Contract Expiration Alert";
                String body = "Dear " + user.getFirstName() + ",\n\n" +
                        "The following contract is expiring in 15 days:\n" +
                        "File: " + file.getName() + "\n" +
                        "Effective Date: " + file.getEffectiveDate() + "\n" +
                        "Expiration Date: " + file.getExpirationDate() + "\n\n" +
                        "Please take appropriate action to renew or make necessary changes.\n\n" +
                        "Best regards,\n" +
                        "Your Contract Management System";
                System.out.println("Alert email");
                try {
                    emailSender.sendEmail(user.getEmail(), subject, body);
                } catch (MessagingException e) {
                    info="Cannot send the mail";
                }
            }
        }
    }
    private User getUserForFile(FileDB file){
        UserFileMapping mapping = userFileMappingRepository.findByContentId(file.getId());
        if(mapping != null){
            return userRepository.findById(mapping.getUserId());
        }
        return null;
    }

}