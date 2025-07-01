package com.leonid.workerbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "deploy_user")
public class User {
    private String username;
    @Id
    private Long chatId;
    @OneToMany
    private List<Application> applications;
    private LocalDateTime createTime;

}
