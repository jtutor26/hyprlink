package com.basecamp.HyprLink.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;
    private String age;
    private String pronouns;
    private String bio;
    private String profilePicture;
    private String theme;
    private String backgroundImage;
    private String linkStyle;
    private String textAlign;
    private String buttonColor;
    private String fontFamily;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<SocialLink> socialLinks;
}