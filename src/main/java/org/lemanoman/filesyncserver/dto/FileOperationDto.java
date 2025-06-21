package org.lemanoman.filesyncserver.dto;

public record FileOperationDto(String sourcePath,
                               String targetPath,
                               Long fileSize,
                               String operation) {
}
