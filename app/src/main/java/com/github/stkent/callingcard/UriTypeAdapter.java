package com.github.stkent.callingcard;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class UriTypeAdapter extends TypeAdapter<Uri> {

    @Override
    public void write(final JsonWriter out, @Nullable final Uri uri) throws IOException {
        if (uri == null) {
            out.nullValue();
            return;
        }

        out.value(uri.toString());
    }

    @Override
    public Uri read(final JsonReader in) throws IOException {
        final JsonToken nextToken = in.peek();

        if (nextToken == JsonToken.STRING) {
            final String uriString = in.nextString();
            return Uri.parse(uriString);
        } else {
            in.skipValue();
            return null;
        }
    }

}
