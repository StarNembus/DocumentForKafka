package com.example.DocumentForKafka.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class FileFromService {
    private String fileName;
    private String fileBody;
}
