/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fnproject.fn.examples;

import com.fnproject.fn.api.RuntimeContext;
import com.fnproject.fn.api.httpgateway.HTTPGatewayContext;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayOutputStream;

public class QRGen {
    private final String defaultFormat;

    public QRGen(RuntimeContext ctx) {
        defaultFormat = ctx.getConfigurationByKey("FORMAT").orElse("png");
    }

    public byte[] create(HTTPGatewayContext hctx) {
        ImageType type = getFormat(hctx.getQueryParameters().get("format").orElse(defaultFormat));
        System.err.println("Default format: " + type.toString());
        String contents = hctx.getQueryParameters().get("contents").orElseThrow(() -> new RuntimeException("Contents must be provided to the QR code"));

        ByteArrayOutputStream stream = QRCode.from(contents).to(type).stream();
        System.err.println("Generated QR Code for contents: " + contents);

        hctx.setResponseHeader("Content-Type", getMimeType(type));
        return stream.toByteArray();
    }

    private ImageType getFormat(String extension) {
        switch (extension.toLowerCase()) {
            case "png":
                return ImageType.PNG;
            case "jpg":
            case "jpeg":
                return ImageType.JPG;
            case "gif":
                return ImageType.GIF;
            case "bmp":
                return ImageType.BMP;
            default:
                throw new RuntimeException(String.format("Cannot use the specified format %s, must be one of png, jpg, gif, bmp", extension));
        }
    }

    private String getMimeType(ImageType type) {
        switch (type) {
            case JPG:
                return "image/jpeg";
            case GIF:
                return "image/gif";
            case PNG:
                return "image/png";
            case BMP:
                return "image/bmp";
            default:
                throw new RuntimeException("Invalid ImageType: " + type);
        }
    }
}
