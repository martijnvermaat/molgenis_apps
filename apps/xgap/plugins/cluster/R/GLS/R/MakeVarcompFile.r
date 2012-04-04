MakeVarcompFile <- function(file.name,var.comp.values=c(1,0.1)) {
# write the values of the variance components contained in var.comp.values to the file file.name (for use in scan_GLS)
    var.comp.values      <- data.frame(var.comp=as.numeric(unlist(var.comp.values)))
    row.names(var.comp.values)<- c("sigma2_g","sigma2_e")
    write.table(var.comp.values,file=file.name,quote=F,row.names = T,col.names=F,sep=",")
}