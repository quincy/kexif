package com.quakbo.kexif.test.condition

import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.full.findAnnotation

@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(ImageLibraryAvailableCondition::class)
annotation class TestIfEnvVariableSet(val environmentVariable: TestEnvironmentVariable)

class ImageLibraryAvailableCondition : ExecutionCondition {
    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val environmentVariable = context.element::class.findAnnotation<TestIfEnvVariableSet>()?.environmentVariable
            ?: throw IllegalStateException("Could not find TestIfEnvVariableSet annotation.")

        return when (System.getenv(environmentVariable.name) != null) {
            true -> ConditionEvaluationResult.enabled("$environmentVariable environment variable found.")
            false -> ConditionEvaluationResult.disabled(
                "$environmentVariable environment variable NOT found.",
                "To enable this test, set the $environmentVariable environment variable to ${environmentVariable.description}"
            )
        }
    }
}

enum class TestEnvironmentVariable(val description: String) {
    IMAGE_METADATA_CORPUS("the location of the local copy of https://github.com/drewnoakes/metadata-extractor-images"),
    ;
}