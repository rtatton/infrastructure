package org.cirrus.infrastructure.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@JsonSerialize
@Value.Style(
    get = {"is*", "has*", "*"},
    strictBuilder = true,
    typeAbstract = "*",
    depluralize = true,
    visibility = ImplementationVisibility.PACKAGE,
    allParameters = true,
    newBuilder = "create",
    jdkOnly = true,
    defaults = @Value.Immutable(copy = false, lazyhash = true))
public @interface ImmutableStyle {}
