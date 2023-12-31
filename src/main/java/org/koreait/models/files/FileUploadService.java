package org.koreait.models.files;

import lombok.RequiredArgsConstructor;
import org.koreait.entities.FileInfo;
import org.koreait.repositories.FileInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jdk.internal.jrtfs.JrtFileAttributeView.AttrID.extension;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileInfoRepository repository;
    private final FileInfoService infoService;

    //썸네일 생성 사이즈
//    private

    public List<FileInfo> upload(MultipartFile[] files, String gid, String location) {
        gid = gid == null || gid.isBlank() ? UUID.randomUUID().toString() : gid;

        List<FileInfo> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileType = file.getContentType();
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            /** 파일 정보 저장 처리 S */
            FileInfo item = FileInfo.builder()
                    .fileName(fileName)
                    .fileType(fileType)
                    .extension(extension)
                    .gid(gid)
                    .location(location)
                    .build();

            repository.saveAndFlush(item);

            infoService.addFileInfo(item); // 파일 경로, 파일 URL 등의 추가 정보
            /** 파일 정보 저장 처리 E */

            /** 파일 업로드 처리 S */
            try {
                file.transferTo(new File(item.getFilePath()));


            } catch (IOException e) {
                e.printStackTrace();
            }
            /** 파일 업로드 처리 E */
        }

        return uploadedFiles;
    }

    public List<FileInfo> upload(MultipartFile[] files, String gid) {
        return upload(files, gid, null);
    }

    public List<FileInfo> upload(MultipartFile[] files) {
        return upload(files, null);
    }
}
