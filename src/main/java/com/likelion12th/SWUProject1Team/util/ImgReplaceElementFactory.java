package com.likelion12th.SWUProject1Team.util;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ImgReplaceElementFactory implements ReplacedElementFactory {

    @Value("${image.imagefolder}")
    private String imageFolder;

    private final ResourceLoader resourceLoader;

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox, UserAgentCallback userAgentCallback, int i, int i1) {
        Element e =blockBox.getElement();
        if (e == null)
            return null;

        String nodeName = e.getNodeName();
        if(nodeName.equals("img")) {
            String attribute = e.getAttribute("src");
            FSImage fsImage;

            try{
                fsImage = buildImage(attribute, userAgentCallback);
            } catch(BadElementException e2) {
                fsImage = null;
            }

            if (fsImage != null) {
                if (i != -1 || i1 != -1) {
                    fsImage.scale(i, i1);
                }
                return new ITextImageElement(fsImage);
            }
        }
        return null;
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener formSubmissionListener) {

    }

    private FSImage buildImage(String srcAttr, UserAgentCallback uac) {
        FSImage fsImage;
        String tmpUrl = srcAttr;

        int index = tmpUrl.indexOf("/images/");

        if (index != -1) {

            tmpUrl = srcAttr.substring(index + "/images/".length());
        }

        String classpathImage = "classpath:static" + imageFolder + tmpUrl;
        Resource resource = resourceLoader.getResource(classpathImage);

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            // base64로 변환
            fsImage = new ITextFSImage(Image.getInstance(imageBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fsImage;

    }

    @Override
    public void reset() {

    }

    @Override
    public void remove(Element element) {

    }
}
