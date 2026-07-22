package com.gudnuz.api.service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OgImageService {

    public String extractImageUrl (String sourceUrl) {

        String imageUrl = null;

            try {
                // 1. Télécharger la page et parser le HTML
                Document doc = Jsoup.connect(sourceUrl).userAgent("Mozilla/5.0").timeout(2000).get();

                // 2. Sélectionner la balise <meta property="og:image">
                Element meta = doc.selectFirst("meta[property=og:image]");

                if (meta != null) {
                    imageUrl = meta.attr("content");

                } else {
                    meta = doc.selectFirst("meta[name=twitter:image]");
                        if (meta == null) {
                            imageUrl = null;
                        }else {
                            imageUrl = meta.attr("content");
                        }



                }

            } catch (IOException e) {

            }

            return imageUrl;

    }

}
