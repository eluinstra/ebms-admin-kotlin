package nl.clockwork.ebms.admin.components

import com.vaadin.flow.component.AbstractCompositeField
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.Component
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.Binder.BindingBuilder
import com.vaadin.flow.function.SerializablePredicate
import java.time.LocalDateTime
import java.util.function.UnaryOperator
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedType


interface WithBinder {
    fun <T> createBinder(c: Class<T>?): Binder<T> {
        return Binder(c)
    }

    fun <T> createBinder(c: Class<T>?, o: T): Binder<T>? {
        val binder = createBinder(c)
        binder.bean = o
        return binder
    }

    fun bind(binder: Binder<*>, component: AbstractField<*, CapturedType>, propertyName: String): Component {
        bind(binder, component, propertyName, UnaryOperator { it }) //identity(it)
        return component
    }

    fun <T> identity(x: T): T = x

    fun <T> bind(
        binder: Binder<*>,
        component: AbstractField<*, T>?,
        propertyName: String?,
        builder: UnaryOperator<BindingBuilder<*, T>>
    ): Component? {
        builder.apply(binder.forField(component)).bind(propertyName)
        return component
    }

    fun bind(binder: Binder<*>, component: AbstractCompositeField<*, *, CapturedType>, propertyName: String): Component {
        bind(binder, component, propertyName, UnaryOperator { it }) //identity(it)
        return component
    }

    fun <T> bind(
        binder: Binder<*>,
        component: AbstractCompositeField<*, *, T>?,
        propertyName: String?,
        builder: UnaryOperator<BindingBuilder<*, T>>
    ): Component? {
        builder.apply(binder.forField(component)).bind(propertyName)
        return component
    }

    fun bind(
        binder: Binder<*>,
        from: AbstractCompositeField<*, *, LocalDateTime?>,
        to: AbstractCompositeField<*, *, LocalDateTime?>,
        propertyName: String,
        validator: SerializablePredicate<in LocalDateTime?>,
        errorMessage: String
    ): Component? {
        val b = binder.forField(to)
            .withValidator(validator, errorMessage)
            .bind(propertyName)
        from.addValueChangeListener { b.validate() }
        return to
    }
}
