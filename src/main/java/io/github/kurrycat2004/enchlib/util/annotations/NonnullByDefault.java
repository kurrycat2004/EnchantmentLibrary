package io.github.kurrycat2004.enchlib.util.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// This annotation is a copy of javax.annotation.ParametersAreNonnullByDefault with the METHOD target added

@Documented
@Nonnull
@TypeQualifierDefault({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonnullByDefault {}

