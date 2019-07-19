package com.aimir.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.ReferencedByPk;

/**
 * 모델간의 관계 찾기 클래스
 * @author YeonKyoung Park(goodjob)
 * @param <T>
 *
 */
public class ObjectRelations<T> {
    private static Log log = LogFactory.getLog(ObjectRelations.class);

    private Class<?> theClass;
    public ObjectRelations(Class<?> theClass){
        this.theClass = theClass;
    }

    @SuppressWarnings({ "hiding", "unused" })
    private <T extends AnnotatedElement> Collection<T> getAnnotatedElements(
            final T[] elements, final Class<? extends Annotation> annotationType) {
        final Collection<T> annotatedElements = new LinkedList<T>();
        for (final T element : elements) {
            if (element.isAnnotationPresent(annotationType)) {
                annotatedElements.add(element);
            }
        }
        return annotatedElements;
    }

    private Annotation getAnnotation(
            Field element, Class<? extends Annotation> annotationType) {
        Annotation anno = null;
        if (element.isAnnotationPresent(annotationType)) {
            anno = element.getAnnotation(annotationType);
        }
        return anno;
    }

    public Class<?> getRelationClass(String attrName) {
        final Class<?> fieldType = getRelationClass(attrName, theClass);
        return fieldType;
    }

    public Class<?> getRelationClass(String attrName,Class<?> checkClass){

        try {
            final Field field = checkClass.getDeclaredField(attrName);
            if(field != null){
                Class<?> fieldType = field.getType();
                return fieldType;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            if (checkClass.getSuperclass() == null) {
                return null;
            } else {
                final Class<?> superfieldType = getRelationClass(attrName,checkClass.getSuperclass());
                return superfieldType;
            }
        }
        return null;
    }

    public String getReferencedBy(String attrName){
        final String refName = getReferencedBy(attrName, theClass);
        return refName;
    }

    public String getReferencedByPk(Method method, Class<?> paramType){

        if(TypeCast.match(paramType.getName()) ||
                paramType.getName().startsWith("java.lang") ||
                paramType.getSimpleName().equals("Type")){
            return null;
        }

        Annotation[][] annoss = method.getParameterAnnotations();
        if(annoss != null && annoss.length >=1 && annoss[0].length > 0){
            ReferencedByPk ref = (ReferencedByPk) annoss[0][0];
            if(ref != null){
                return ref.name();
            }else{
                return null;
            }
        }else{
            return null;
        }

    }

    public String getReferencedBy(String attrName, Class<?> checkClass ){

        try {
            final Field field = checkClass.getDeclaredField(attrName);
            if(field != null){
                ReferencedBy ref =  (ReferencedBy) getAnnotation(
                        field, ReferencedBy.class);
                return ref.name();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            if (checkClass.getSuperclass() == null) {
                return null;
            } else {
                final String value = getReferencedBy(attrName,checkClass.getSuperclass());
                return value;
            }
        }

        return null;
    }

    public boolean checkRelationOrObject(String attrName) {

        final boolean value = checkRelationOrObject(attrName, theClass);
        return value;
    }

    public boolean checkRelationOrObject(String attrName,Class<?> checkClass ){

        try {
            final Field field = checkClass.getDeclaredField(attrName);
            if(field != null){
                log.info("Object[" + checkClass.getSimpleName() + "] Field["+field.getName()+"]");
                Class<?> fieldType = field.getType();
                if(TypeCast.match(fieldType.getName()) ||
                        fieldType.getName().startsWith("java.lang") ||
                        fieldType.getName().startsWith("com.aimir.constants.CommonConstants") ||
                        fieldType.getSimpleName().equals("Type")){
                    return false;
                }
                else{
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            if (checkClass.getSuperclass() == null) {
                return false;
            } else {
                final boolean value = checkRelationOrObject(attrName,checkClass.getSuperclass());
                return value;
            }
        }
        return false;
    }

}
