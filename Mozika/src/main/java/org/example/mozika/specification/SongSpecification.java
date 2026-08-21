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
            System.out.println("🔍 TEMPLATE DEBUG: name=id | columnType=[int8] | isText=false | isNumeric=true");

            
            if(object.getId()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("id"), object.getId()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=artistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getArtistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artistidArtists"), object.getArtistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=albumidAlbums | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getAlbumidAlbums()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("albumidAlbums"), object.getAlbumidAlbums()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=title | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("title"), object.getTitle(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=durationSeconds | columnType=[int4] | isText=false | isNumeric=true");

            
            if(object.getDurationSeconds()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("durationSeconds"), object.getDurationSeconds()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("durationSeconds"), object.getDurationSecondsMin(), object.getDurationSecondsMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=releaseDate | columnType=[date] | isText=false | isNumeric=false");

            
            if(object.getReleaseDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("releaseDate"), object.getReleaseDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("releaseDate"), object.getReleaseDateMin(), object.getReleaseDateMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=language | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("language"), object.getLanguage(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=authorComposer | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("authorComposer"), object.getAuthorComposer(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=musicalArranger | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("musicalArranger"), object.getMusicalArranger(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=recordingLocation | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("recordingLocation"), object.getRecordingLocation(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=recordingDate | columnType=[date] | isText=false | isNumeric=false");

            
            if(object.getRecordingDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("recordingDate"), object.getRecordingDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("recordingDate"), object.getRecordingDateMin(), object.getRecordingDateMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=storagetypeidAudioStorageTypes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStoragetypeidAudioStorageTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("storagetypeidAudioStorageTypes"), object.getStoragetypeidAudioStorageTypes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=audioUrl | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("audioUrl"), object.getAudioUrl(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=audioFile | columnType=[bytea] | isText=false | isNumeric=false");

            
            if(object.getAudioFile()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("audioFile"), object.getAudioFile()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=videoUrl | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("videoUrl"), object.getVideoUrl(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=karaokeAudio | columnType=[bytea] | isText=false | isNumeric=false");

            
            if(object.getKaraokeAudio()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("karaokeAudio"), object.getKaraokeAudio()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=lyrics | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("lyrics"), object.getLyrics(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=lyricsSyncData | columnType=[jsonb] | isText=false | isNumeric=false");

            
            if(object.getLyricsSyncData()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        criteriaBuilder.function("jsonb", String.class, root.get("lyricsSyncData")),
						criteriaBuilder.function("jsonb", String.class, criteriaBuilder.literal(object.getLyricsSyncData()))
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=solfa | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("solfa"), object.getSolfa(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=playback | columnType=[bytea] | isText=false | isNumeric=false");

            
            if(object.getPlayback()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("playback"), object.getPlayback()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=isDownloadable | columnType=[bool] | isText=false | isNumeric=false");

            
            if(object.getIsDownloadable()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isDownloadable"), object.getIsDownloadable()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=createdAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
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
