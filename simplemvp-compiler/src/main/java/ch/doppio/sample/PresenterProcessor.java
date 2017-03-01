package ch.doppio.sample;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedAnnotationTypes("ch.doppio.sample.PresenterView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PresenterProcessor extends AbstractProcessor {

    private static final String EMPTY_VIEW_CLASS_NAME_PREFIX = "Empty";

    private Elements mElementUtils;
    private Filer mFiler;
    private Types mTypeUtils;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mTypeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        final Set<? extends Element> views = roundEnv.getElementsAnnotatedWith(PresenterView.class);
        for(TypeElement presenterView : ElementFilter.typesIn(views)) {
            if(presenterView.getKind() == ElementKind.INTERFACE) {
                writeSourceFile(presenterView);
            }
        }

        return true;
    }

    private MethodSpec generateMethodSpec(final ExecutableElement method) {
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        final TypeMirror returnType = method.getReturnType();
        switch (returnType.getKind()) {
            case BOOLEAN:
                methodBuilder.addStatement("return false");

                break;
            case VOID:
                break;
            default:
                methodBuilder.addStatement("return null");
        }
        methodBuilder.returns(getTypeName(returnType));

        final List<? extends VariableElement> parameters = method.getParameters();
        for(VariableElement parameter : parameters) {
            final String parameterName = parameter.getSimpleName().toString();
            final TypeName parameterType = getTypeName(parameter);
            methodBuilder.addParameter(parameterType, parameterName, Modifier.FINAL);
        }

        return methodBuilder.build();
    }

    private TypeSpec.Builder generateTypeSpec(final Element viewInterface) {
        return TypeSpec.classBuilder(EMPTY_VIEW_CLASS_NAME_PREFIX + getSimpleName(viewInterface)).
                addModifiers(Modifier.PUBLIC).
                addSuperinterface(getTypeName(viewInterface));
    }

    private String getPackage(final Element element) {
        return mElementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    private String getSimpleName(final Element element) {
        return element.getSimpleName().toString();
    }

    private TypeName getTypeName(final Element element) {
        return TypeName.get(element.asType());
    }

    private TypeName getTypeName(final TypeMirror typeMirror) {
        return TypeName.get(typeMirror);
    }

    private void writeSourceFile(final TypeElement viewInterface) {
        final TypeSpec.Builder emptyViewClass = generateTypeSpec(viewInterface);

        for (Element method : viewInterface.getEnclosedElements()) {
            final MethodSpec methodSpec = generateMethodSpec((ExecutableElement) method);
            emptyViewClass.addMethod(methodSpec);
        }

        List<? extends TypeMirror> interfaces = viewInterface.getInterfaces();
        while(!interfaces.isEmpty() && interfaces.size() == 1) {
            final TypeElement baseViewInterface = (TypeElement) mTypeUtils.asElement(interfaces.get(0));
            for (Element method : baseViewInterface.getEnclosedElements()) {
                final MethodSpec methodSpec = generateMethodSpec((ExecutableElement) method);
                emptyViewClass.addMethod(methodSpec);
            }

            interfaces = baseViewInterface.getInterfaces();
        }

        final JavaFile javaFile = JavaFile.builder(getPackage(viewInterface), emptyViewClass.build()).
                build();

        try {
            javaFile.writeTo(mFiler);
        } catch (final IOException e) {
            // ignore
        }
    }

}