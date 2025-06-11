package org.lemanoman.filesyncserver.dto;

public class LocationDto {
    private Long id;
    private String name;
    private String pathkey;
    private String fullPath;

    public LocationDto() {
    }

    public LocationDto(Long id, String name, String pathkey, String fullPath) {
        this.id = id;
        this.name = name;
        this.pathkey = pathkey;
        this.fullPath = fullPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathkey() {
        return pathkey;
    }

    public void setPathkey(String pathkey) {
        this.pathkey = pathkey;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
