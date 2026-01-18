package com.THLight.BLE.USBeacon.Writer.Simple.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlParser {
    protected List<Map<String, String>> mapList = new ArrayList<>();
    private Map<String, String> map = new LinkedHashMap<>();

    // 如若要抓全部可以使用  -> XmlToJson
    // 只抓指定的參數放進Map -> XmlParser
    public void parse(InputStream inputStream, String... parameters) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            parserData(parser, parameters);
        } finally {
            inputStream.close();
        }
    }

    private void parserData(XmlPullParser parser, String... parameters) throws IOException, XmlPullParserException {
        String tagName = "null";
        while (true) {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                tagName = parser.getName();
                parser.next();
            } else if (parser.getEventType() == XmlPullParser.TEXT) {
                addValue(tagName, parser.getText(), parameters);
                parser.next();
            } else if (parser.getEventType() == XmlPullParser.END_TAG) {
                parser.next();
            } else if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                break;
            }
        }
    }

    private void addValue(String tagName, String text, String[] parameters) {
        if (map.size() == parameters.length) {
            mapList.add(map);
            map = new LinkedHashMap<>();
        }
        for (String parameter : parameters) {
            if (StringUtil.isEquals(parameter, tagName)) {
                map.put(tagName, text);
            }
        }
    }

    public List<Map<String, String>> getMapList() {
        return mapList;
    }
}
