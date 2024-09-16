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
public class AppAudio implements AppMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;
    private String fileName;

    private String downloadLink;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser appUser;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
}
