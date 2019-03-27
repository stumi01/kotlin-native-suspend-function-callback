package de.jensklingenberg.compiler.common

import de.jensklingenberg.compiler.kaptmpp.AbstractProcessor
import de.jensklingenberg.compiler.kaptmpp.Element
import de.jensklingenberg.compiler.kaptmpp.RoundEnvironment
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.source.PsiSourceFile

class ClassParser() {


    companion object {
        val TAG = "dd"


        fun parse(descriptor: ClassDescriptor, generator: AbstractProcessor, roundEnvironment: RoundEnvironment) {
            val targetClass = descriptor

            val buildFolder = targetClass.guessingProjectFolder() + "build/"
            generator.processingEnv.projectFolder = targetClass.guessingProjectFolder()
            generator.processingEnv.buildFolder = buildFolder

            generator.getSupportedAnnotationTypes().forEach { annotation ->
                if (targetClass.hasAnnotation(annotation)) {
                    roundEnvironment.module = descriptor.module
                    roundEnvironment.elements.add(Element.ClassElement(
                            targetClass.name.asString(),
                            annotation = targetClass.findAnnotation(annotation),
                            descriptor = targetClass
                    ))
                    generator.process(roundEnvironment)
                }

            }

        }



        fun parseMethod(descriptor: ClassDescriptor, function: FunctionDescriptor, generator: AbstractProcessor, roundEnvironment: RoundEnvironment) {

            generator.getSupportedAnnotationTypes().forEach { annotationNames ->

                if (function.annotations.hasAnnotation(FqName(annotationNames))) {
                    val annotation = function.annotations.findAnnotation(FqName(annotationNames))

                    roundEnvironment.also {
                        it.module = descriptor.module


                        it.elements.add(Element.FunctionElement(
                                simpleName = function.name.asString(),
                                annotation = annotation,
                                descriptor = descriptor
                        ))
                    }

                    generator.process(roundEnvironment)

                }

            }


        }

    }


}