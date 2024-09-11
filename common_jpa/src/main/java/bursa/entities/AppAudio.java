package bursa.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_audio")
public class AppAudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser appUser;
    private String fileName;
    private String downloadLink;
    @OneToOne
    private BinaryContent binaryContent;
    private Long fileSize;
    private String mimeType;
    private Integer duration;
}
