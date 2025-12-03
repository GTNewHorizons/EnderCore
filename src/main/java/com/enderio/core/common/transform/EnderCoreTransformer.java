package com.enderio.core.common.transform;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EnderCoreTransformer implements IClassTransformer {

    protected static class ObfSafeName {

        private String deobf, srg;

        public ObfSafeName(String deobf, String srg) {
            this.deobf = deobf;
            this.srg = srg;
        }

        public String getName() {
            return EnderCorePlugin.runtimeDeobfEnabled ? srg : deobf;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof String) {
                return obj.equals(deobf) || obj.equals(srg);
            } else if (obj instanceof ObfSafeName) {
                return ((ObfSafeName) obj).deobf.equals(deobf) && ((ObfSafeName) obj).srg.equals(srg);
            }
            return false;
        }

        // no hashcode because I'm naughty
    }

    protected static abstract class Transform {

        abstract void transform(Iterator<MethodNode> methods);
    }

    private static final String anvilContainerClass = "net.minecraft.inventory.ContainerRepair";
    private static final ObfSafeName anvilContainerMethod = new ObfSafeName("updateRepairOutput", "func_82848_d");

    private static final String anvilGuiClass = "net.minecraft.client.gui.GuiRepair";
    private static final ObfSafeName anvilGuiMethod = new ObfSafeName(
            "drawGuiContainerForegroundLayer",
            "func_146979_b");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Anvil max level
        if (transformedName.equals(anvilContainerClass) || transformedName.equals(anvilGuiClass)) {
            basicClass = transform(basicClass, anvilContainerClass, anvilContainerMethod, new Transform() {

                @Override
                void transform(Iterator<MethodNode> methods) {
                    while (methods.hasNext()) {
                        MethodNode m = methods.next();
                        if (anvilContainerMethod.equals(m.name) || anvilGuiMethod.equals(m.name)) {
                            for (int i = 0; i < m.instructions.size(); i++) {
                                AbstractInsnNode next = m.instructions.get(i);

                                next = m.instructions.get(i);
                                if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 40) {
                                    m.instructions.set(
                                            next,
                                            new MethodInsnNode(
                                                    INVOKESTATIC,
                                                    "com/enderio/core/common/transform/EnderCoreMethods",
                                                    "getMaxAnvilCost",
                                                    "()I",
                                                    false));
                                }
                            }
                        }
                    }
                }
            });
        }

        return basicClass;
    }

    protected final byte[] transform(byte[] classBytes, String className, ObfSafeName methodName,
            Transform transformer) {
        EnderCorePlugin.logger.info("Transforming Class [" + className + "], Method [" + methodName.getName() + "]");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        transformer.transform(methods);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        EnderCorePlugin.logger.info("Transforming " + className + " Finished.");
        return cw.toByteArray();
    }
}
