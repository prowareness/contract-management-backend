package com.devon.contractmanagementsystem.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.ZoneId;

import com.devon.contractmanagementsystem.model.FileDB;
import com.devon.contractmanagementsystem.model.UserFileMapping;
import com.devon.contractmanagementsystem.repository.FileDBRepository;
import com.devon.contractmanagementsystem.repository.UserFileMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private UserFileMappingRepository userFileMappingRepository;

    public FileDB store(MultipartFile file, int userId, Date effectiveDate, Date expirationDate) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        System.out.println("userId: "+userId);
        FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes());
        fileDB.setEffectiveDate((effectiveDate));
        fileDB.setExpirationDate(expirationDate);
        fileDB = fileDBRepository.save(fileDB);

        return fileDB;
    }

    public FileDB getFile(String id) {
        return (FileDB) fileDBRepository.findById(id).get();
    }

    public FileDB getFileByName(String name){
        Optional<FileDB> fileDBOptional = fileDBRepository.findByName(name);
        return fileDBOptional.orElse(null);
    }

    public Stream<FileDB> getUserFiles(int userId) {
        List<String> fileIds = userFileMappingRepository.findByUserId(userId)
                .stream()
                .map(UserFileMapping::getContentId)
                .collect(Collectors.toList());
        return fileDBRepository.findByIdIn(fileIds).stream();
    }

    public void saveFile(FileDB fileDB){
        fileDBRepository.save(fileDB);
    }

    public List<FileDB> getExpiringFiles(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);

        Date expirationDate = calendar.getTime();
        return fileDBRepository.findByExpirationDateBefore(expirationDate);
    }



}