//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javax.validation;

public interface ConstraintValidatorContext {
    void disableDefaultConstraintViolation();

    String getDefaultConstraintMessageTemplate();

    ClockProvider getClockProvider();

    ConstraintValidatorContext.ConstraintViolationBuilder buildConstraintViolationWithTemplate(String var1);

    <T> T unwrap(Class<T> var1);

    public interface ConstraintViolationBuilder {
        /** @deprecated */
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext addNode(String var1);

        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

        ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

        ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext addParameterNode(int var1);

        ConstraintValidatorContext addConstraintViolation();

        public interface ContainerElementNodeContextBuilder {
            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderDefinedContext atKey(Object var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderDefinedContext atIndex(Integer var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface ContainerElementNodeBuilderCustomizableContext {
            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeContextBuilder inIterable();

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface ContainerElementNodeBuilderDefinedContext {
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface NodeContextBuilder {
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext atKey(Object var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext atIndex(Integer var1);

            /** @deprecated */
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface NodeBuilderCustomizableContext {
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeContextBuilder inIterable();

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext inContainer(Class<?> var1, Integer var2);

            /** @deprecated */
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface NodeBuilderDefinedContext {
            /** @deprecated */
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(String var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext addBeanNode();

            ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String var1, Class<?> var2, Integer var3);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface LeafNodeContextBuilder {
            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderDefinedContext atKey(Object var1);

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderDefinedContext atIndex(Integer var1);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface LeafNodeBuilderCustomizableContext {
            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeContextBuilder inIterable();

            ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext inContainer(Class<?> var1, Integer var2);

            ConstraintValidatorContext addConstraintViolation();
        }

        public interface LeafNodeBuilderDefinedContext {
            ConstraintValidatorContext addConstraintViolation();
        }
    }
}
