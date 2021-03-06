/*
 *    Copyright 2013-2014 Parisoft Team
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.parisoft.resty.entity;

import static com.github.parisoft.resty.utils.ObjectUtils.isInstanciableFromString;
import static com.github.parisoft.resty.utils.ObjectUtils.newInstanceFromString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.CONTENT_ENCODING;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.parisoft.resty.utils.JacksonUtils;
import com.github.parisoft.resty.utils.MediaTypeUtils;

public class EntityReaderImpl implements EntityReader {

    private final HttpResponse httpResponse;
    private String body;

    public EntityReaderImpl(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public String getEntityAsString() throws IOException {
        if (body == null) {
            final HttpEntity entity = httpResponse.getEntity();

            if (entity == null) {
                return body = "";
            }

            try {
                final HttpEntity entityWrapper = isGzipEncoding() ? new GzipDecompressingEntity(entity) : entity;
                final ByteArrayOutputStream output = new ByteArrayOutputStream();

                entityWrapper.writeTo(output);

                body = output.toString(getContentCharSet().toString());
            } finally {
                EntityUtils.consume(entity);
            }
        }

        return body;
    }

    @Override
    public <T> T getEntityAs(Class<T> someClass) throws IOException {
        if (body != null) {
            return getEntityFromBodyAs(someClass);
        }

        final HttpEntity entity = httpResponse.getEntity();

        if (entity == null) {
            return null;
        }

        try {
            final HttpEntity entityWrapper = isGzipEncoding() ? new GzipDecompressingEntity(entity) : entity;

            return JacksonUtils.read(entityWrapper, someClass, MediaTypeUtils.valueOf(getContentType()));
        } catch (Exception e) {
            if (isInstanciableFromString(someClass)) {
                return newInstanceFromString(someClass, getEntityAsString());
            }

            throw new IOException("Cannot read response entity", e);
        } finally {
            EntityUtils.consume(entity);
        }
    }

    private <T> T getEntityFromBodyAs(Class<T> someClass) throws IOException {
        try {
            return JacksonUtils.read(body, someClass, MediaTypeUtils.valueOf(getContentType()));
        } catch (Exception e) {
            throw new IOException("Cannot read response entity", e);
        }
    }

    @Override
    public <T> T getEntityAs(TypeReference<T> reference) throws IOException {
        if (body != null) {
            return getEntityFromBodyAs(reference);
        }

        final HttpEntity entity = httpResponse.getEntity();

        if (entity == null) {
            return null;
        }

        try {
            final HttpEntity entityWrapper = isGzipEncoding() ? new GzipDecompressingEntity(entity) : entity;

            return JacksonUtils.read(entityWrapper, reference, MediaTypeUtils.valueOf(getContentType()));
        } catch (Exception e) {
            throw new IOException("Cannot read response entity", e);
        } finally {
            EntityUtils.consume(entity);
        }
    }

    private <T> T getEntityFromBodyAs(TypeReference<T> reference) throws IOException {
        try {
            return JacksonUtils.read(body, reference, MediaTypeUtils.valueOf(getContentType()));
        } catch (Exception e) {
            throw new IOException("Cannot read response entity", e);
        }
    }

    private boolean isGzipEncoding() {
        return "gzip".equalsIgnoreCase(getContentEncoding().getValue());
    }

    @Override
    public Charset getContentCharSet() {
        final Charset charset = getContentType().getCharset();

        if (charset != null) {
            return charset;
        }

        return UTF_8;
    }

    @Override
    public ContentType getContentType() {
        try {
            return ContentType.parse(httpResponse.getFirstHeader(CONTENT_TYPE).getValue());
        } catch (Exception e) {
            return ContentType.getLenientOrDefault(httpResponse.getEntity());
        }
    }

    @Override
    public Header getContentEncoding() {
        final HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            final Header encoding = entity.getContentEncoding();

            if (encoding != null) {
                return encoding;
            }
        }

        final Header contentEncoding = httpResponse.getFirstHeader(CONTENT_ENCODING);

        if (contentEncoding != null) {
            return contentEncoding;
        }

        return new BasicHeader(CONTENT_ENCODING, "");
    }

    @Override
    protected void finalize() throws Throwable {
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        super.finalize();
    }
}
