package com.basecamp.LinkGarden.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "social_links")
@Data
public class SocialLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
}
