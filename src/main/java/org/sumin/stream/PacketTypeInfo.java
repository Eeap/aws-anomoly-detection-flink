package org.sumin.stream;

import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ListTypeInfo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class PacketTypeInfo extends TypeInfoFactory<List> {
    @Override
    public TypeInformation<List> createTypeInfo(Type type, Map<String, TypeInformation<?>> map) {
        return new ListTypeInfo(TypeInformation.of(Integer.class));
    }
}
