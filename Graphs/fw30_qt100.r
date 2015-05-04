# Author : G Asish Kumar and Chinmaya Reddy*/
# Description: Plot to show Accuracy vs At least N no of tags predicted/suggested correctly

 x <- c(1, 2, 3, 4, 5)
 y <- c(87.31390298086806,32.05159071367154,4.897517981125016,0.32679738562091504,0.05564056196967589)
 plot(x, y, type='o', pch=1, col="blue", xlab="At least 'N' no of Tags Predicted Correctly", ylab="Accuracy(%)", ylim=c(0, 100), xlim=c(1,5))
 title(main="fwt = 30   qtt = 100", col.main="blue", font.main=4)