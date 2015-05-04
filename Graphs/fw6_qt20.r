# Author : G Asish Kumar and Chinmaya Reddy*/
# Description: Plot to show Accuracy vs At least X no of tags predicted/suggested correctly

 x <- c(1, 2, 3, 4, 5)
 y <- c(87.25753506415995,34.41788478073947,0.6993464052287581,0.05564056196967589)
 plot(x, y, type='o', pch=1, col="blue", xlab="At least X no of Tags Predicted Correctly", ylab="Accuracy(%)", ylim=c(0, 100), xlim=c(0,5))
 title(main="FT 6 QTT 20", col.main="blue", font.main=4)