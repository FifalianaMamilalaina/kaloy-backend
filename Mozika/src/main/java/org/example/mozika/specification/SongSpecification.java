package org.example.mozika.specification;

import org.example.mozika.models.Song;
import org.example.mozika.models.dto.SongSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class SongSpecification {

    public static Specification<Song> filter(SongSearch object) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(object.getId()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("id"), object.getId()
                    )
                );
            }

            if(object.getArtistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artistidArtists"), object.getArtistidArtists()
                    )
                );
            }

            if(object.getAlbumidAlbums()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("albumidAlbums"), object.getAlbumidAlbums()
                    )
                );
            }

            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("title"), object.getTitle(), predicates);

            if(object.getDurationSeconds()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("durationSeconds"), object.getDurationSeconds()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("durationSeconds"), object.getDurationSecondsMin(), object.getDurationSecondsMax(), predicates);

            if(object.getReleaseDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("releaseDate"), object.getReleaseDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("releaseDate"), object.getReleaseDateMin(), object.getReleaseDateMax(), predicates);

            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("language"), object.getLanguage(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("authorComposer"), object.getAuthorComposer(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("musicalArranger"), object.getMusicalArranger(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("recordingLocation"), object.getRecordingLocation(), predicates);

            if(object.getRecordingDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("recordingDate"), object.getRecordingDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("recordingDate"), object.getRecordingDateMin(), object.getRecordingDateMax(), predicates);

            if(object.getStoragetypeidAudioStorageTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("storagetypeidAudioStorageTypes"), object.getStoragetypeidAudioStorageTypes()
                    )
                );
            }

            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("audioUrl"), object.getAudioUrl(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("videoUrl"), object.getVideoUrl(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("karaokeAudioUrl"), object.getKaraokeAudioUrl(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("lyrics"), object.getLyrics(), predicates);

            if(object.getLyricsSyncData()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        criteriaBuilder.function("jsonb", String.class, root.get("lyricsSyncData")),
                        criteriaBuilder.function("jsonb", String.class, criteriaBuilder.literal(object.getLyricsSyncData()))
                    )
                );
            }

            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("solfaUrl"), object.getSolfaUrl(), predicates);
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("playbackUrl"), object.getPlaybackUrl(), predicates);

            if(object.getIsDownloadable()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isDownloadable"), object.getIsDownloadable()
                    )
                );
            }

            if(object.getCreatedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("createdAt"), object.getCreatedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("createdAt"), object.getCreatedAtMin(), object.getCreatedAtMax(), predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
