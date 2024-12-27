plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.4-neoforge" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "mod"
    ofTask("build")
}

stonecutter.parameters {

}
