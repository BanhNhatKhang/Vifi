package com.example.movie.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtils {
    
    public static String makeSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // chuyển về chữ thường
        String slug = input.toLowerCase(Locale.forLanguageTag("vi"));
        // loại bỏ dấu tiếng việt
        slug = slug.replaceAll("[áàảãạâấầẩẫậăắằẳẵặ]", "a");
        slug = slug.replaceAll("[éèẻẽẹêếềểễệ]", "e");
        slug = slug.replaceAll("[íìỉĩị]", "i");
        slug = slug.replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o");
        slug = slug.replaceAll("[úùủũụưứừửữự]", "u");
        slug = slug.replaceAll("[ýỳỷỹỵ]", "y");
        slug = slug.replaceAll("đ", "d");
        // loại bỏ các dấu còn sót lại
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // thay các ký tự đặc biệt bằng dấu gạch ngang
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        // thay các khoảng trắng bằng dấu gạch ngang
        slug = slug.replaceAll("\\s+", "-");
        // loại bỏ dấu gạch ngang thừa ở đầu và cuối
        slug = slug.replaceAll("^-|-$", "");
        slug = slug.replace("-+", "-");

        return slug;

    }
}
