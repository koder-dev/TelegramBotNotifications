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
@Table(name = "app_doc")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;
    private String docName;
    private String downloadLink;
    @ManyToOne
    @JoinColumn(name="user_id")
    private AppUser appUser;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
}
