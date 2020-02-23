package com.teddy.plugin.mybatis.query;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.query
 * @Description: 分页信息
 * @date 2018-5-4 18:21
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
@ToString
public class Page {
    @NonNull
    private int offset;
    @NonNull
    private int limit;

    private long total;
    private int pages;

    /**
     * Set total.
     *
     * @param total the total
     */
    public void setTotal(long total) {
        this.total = total;
        this.pages = (int)(total % limit == 0 ? total / limit : total / limit + 1);
    }
}
