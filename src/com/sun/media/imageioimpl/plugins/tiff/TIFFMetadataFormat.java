/*
 * $RCSfile: TIFFMetadataFormat.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 05:01:48 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import javax.imageio.metadata.IIOMetadataFormat;
import java.util.*;

public abstract class TIFFMetadataFormat implements IIOMetadataFormat {

    protected Map elementInfoMap = new HashMap();
    protected Map attrInfoMap = new HashMap();

    protected String resourceBaseName;
    protected String rootName;

    public String getRootName() {
        return rootName;
    }

    private String getResource(String key, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {
            ResourceBundle bundle =
                ResourceBundle.getBundle(resourceBaseName, locale);
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private TIFFElementInfo getElementInfo(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("elementName == null!");
        }
        TIFFElementInfo info =
            (TIFFElementInfo)elementInfoMap.get(elementName);
        if (info == null) {
            throw new IllegalArgumentException("No such element: " +
                                               elementName);
        }
        return info;
    }

    private TIFFAttrInfo getAttrInfo(String elementName, String attrName) {
        if (elementName == null) {
            throw new IllegalArgumentException("elementName == null!");
        }
        if (attrName == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        String key = elementName + "/" + attrName;
        TIFFAttrInfo info = (TIFFAttrInfo)attrInfoMap.get(key);
        if (info == null) {
            throw new IllegalArgumentException("No such attribute: " + key);
        }
        return info;
    }

    public int getElementMinChildren(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.minChildren;
    }

    public int getElementMaxChildren(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.maxChildren;
    }

    public String getElementDescription(String elementName, Locale locale) {
        if (!elementInfoMap.containsKey(elementName)) {
            throw new IllegalArgumentException("No such element: " +
                                               elementName);
        }
        return getResource(elementName, locale);
    }

    public int getChildPolicy(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.childPolicy;
    }

    public String[] getChildNames(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.childNames;
    }

    public String[] getAttributeNames(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.attributeNames;
    }

    public int getAttributeValueType(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.valueType;
    }

    public int getAttributeDataType(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.dataType;
    }

    public boolean isAttributeRequired(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.isRequired;
    }

    public String getAttributeDefaultValue(String elementName,
                                           String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.defaultValue;
    }

    public String[] getAttributeEnumerations(String elementName,
                                             String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.enumerations;
    }

    public String getAttributeMinValue(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.minValue;
    }

    public String getAttributeMaxValue(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.maxValue;
    }

    public int getAttributeListMinLength(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.listMinLength;
    }

    public int getAttributeListMaxLength(String elementName, String attrName) {
        TIFFAttrInfo info = getAttrInfo(elementName, attrName);
        return info.listMaxLength;
    }

    public String getAttributeDescription(String elementName, String attrName,
                                          Locale locale) {
        String key = elementName + "/" + attrName;
        if (!attrInfoMap.containsKey(key)) {
            throw new IllegalArgumentException("No such attribute: " + key);
        }
        return getResource(key, locale);
    }

    public int getObjectValueType(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        return info.objectValueType;
    }

    public Class getObjectClass(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectClass;
    }

    public Object getObjectDefaultValue(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectDefaultValue;
    }

    public Object[] getObjectEnumerations(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectEnumerations;
    }

    public Comparable getObjectMinValue(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectMinValue;
    }

    public Comparable getObjectMaxValue(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectMaxValue;
    }

    public int getObjectArrayMinLength(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectArrayMinLength;
    }

    public int getObjectArrayMaxLength(String elementName) {
        TIFFElementInfo info = getElementInfo(elementName);
        if (info.objectValueType == VALUE_NONE) {
            throw new IllegalArgumentException(
                     "Element cannot contain an object value: " + elementName);
        }
        return info.objectArrayMaxLength;
    }

    public TIFFMetadataFormat() {}
}
