package net.myanmar.rummy.logic;

import java.util.Comparator;

public class SortCardAsc implements Comparator<Card> {
//        - Bộ bài để so kết quả gồm có 3 lá
//        - Thứ tự xếp hạng các bộ bài lần lượt là: Xám > TPS >Sảnh > Bộ đầu người  > Thùng > Bài tính điểm
//        1. Xám: 3 lá có cùng phần số.
//        2. Thùng phá sảnh: 3 lá tạo thành sảnh đồng chất. 
//        3. Sảnh: 3 lá tạo thành sảnh không cùng chất
//        4. Bộ đầu người. 3 lá bao gồm các cây J,Q,K
//        5. Thùng: 3 lá cùng chất.
//        6. Bài tính điểm: 3 lá không tạo thành một trong các bộ bài trên. Trong trường hợp này sẽ tính điểm của các lá bài có số. Trong trường hợp bằng điểm nhau sẽ xét đến độ mạnh của lá bài, cây cao nhất là K
//        "
	@Override   
	public int compare(Card o1, Card o2) {
		// TODO Auto-generated method stub
		if(o1.getN() > o2.getN())
			return 1;
		else if(o1.getN() == o2.getN()){
			if(o1.getS() > o2.getS()){
				return 1;
			} else {
				return -1;
			}
		} else 
			return -1;
	}
	
}
