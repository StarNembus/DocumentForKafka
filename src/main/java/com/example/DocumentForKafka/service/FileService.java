package com.example.DocumentForKafka.service;

import com.example.DocumentForKafka.entity.FileFromService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FileService {
   List<String> readFilenames() throws IOException;
   String readContent(String fileName);
   FileFromService transformFileToFileFromService(String name) throws IOException;
   List<FileFromService> getListFileFromService(List<String> fileNames);
   List<String> checkList(List<String> files);
   void deleteFile(String file) throws FileNotFoundException;
   void createBasketDirectory() throws IOException;
   void moveFileToBasket(String fileName) throws IOException;
   String changeSubstringToLowerCase(String fileName);
}
