package com.nuryadincjr.merdekabelanja.resorces;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.models.Headline;

public class Headlines {

    public static Headline[] getHeadlines() {
        return HEADLINE;
    }

    public static final Headline HEADLINE_COLECTION =
            new Headline(R.drawable.clothing, "Diskon Rp.40.000\nuntuk semua clothing");

    public static final Headline HEADLINE_ELECTRONIC =
            new Headline(R.drawable.electronic, "Teknologi Terbaru\nBuy Now Electronic");

    public static final Headline HEADLINE_BOOK =
            new Headline(R.drawable.books, "Membaca secara online\nBuy Now Books");

    public static final Headline[] HEADLINE = {HEADLINE_BOOK, HEADLINE_ELECTRONIC, HEADLINE_COLECTION};
}
