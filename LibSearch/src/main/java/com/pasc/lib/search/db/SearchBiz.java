package com.pasc.lib.search.db;

import android.text.TextUtils;

import com.pasc.lib.search.LocalSearchManager;
import com.pasc.lib.search.util.SearchUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author yangzijian
 * @date 2019/3/4
 * @des
 * @modify
 **/
public class SearchBiz {

    private static SearchSourceItem convertPinyin(SearchSourceItem sourceItem) {
        if (!TextUtils.isEmpty (sourceItem.firstChars) && !TextUtils.isEmpty (sourceItem.pinyins)) {
            return sourceItem;
        }
        return SearchUtil.createCNPinyin (sourceItem);
    }

    public static Single<Integer> addSource(final SearchSourceItem sourceItem) {

        return Single.create (new SingleOnSubscribe<Integer> () {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                convertPinyin (sourceItem);
                sourceItem.insert ();
                emitter.onSuccess (1);
            }
        }).subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                ;

    }

    public static Single<Integer> addSources(final List<SearchSourceItem> sourceItems) {
        return Single.create (new SingleOnSubscribe<Integer> () {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                FlowManager.getDatabase (SearchSourceDB.class).executeTransaction (new ITransaction () {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        for (SearchSourceItem item : sourceItems) {
                            convertPinyin (item);
                            item.insert (databaseWrapper);
                        }
                    }
                });

                emitter.onSuccess (sourceItems.size ());
            }
        }).subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                ;
    }

    /***
     * 根据类型 和 个/企业版 删数据
     * @param type
     * @param entranceLocation
     * @return
     */
    public static Single<Integer> deleteAll(final String type, final String entranceLocation) {
        return Single.create (new SingleOnSubscribe<Integer> () {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                FlowManager.getDatabase (SearchSourceDB.class).executeTransaction (new ITransaction () {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        SQLite.delete (SearchSourceItem.class).where (SearchSourceItem_Table.type.eq (type))
                                .and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation)).execute ();
                    }
                });
                emitter.onSuccess (1);
            }
        }).subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                ;
    }

    public static Single<Long> queryCount(final String type, final String entranceLocation) {
        return Single.create (new SingleOnSubscribe<Long> () {
            @Override
            public void subscribe(SingleEmitter<Long> emitter) throws Exception {
                long count = SQLite.selectCountOf ().from (SearchSourceItem.class).where (SearchSourceItem_Table.type.eq (type))
                        .and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation)).count ();
                emitter.onSuccess (count);
            }
        }).subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                ;
    }

    public static Single<List<SearchSourceItem>> queryLocal(final String keyword, final String entranceLocation, final String type) {

        return Single.create (new SingleOnSubscribe<List<SearchSourceItem>> () {
            @Override
            public void subscribe(SingleEmitter<List<SearchSourceItem>> emitter) throws Exception {

                if (SearchUtil.isEmpty (keyword)) {
                    emitter.onSuccess (new ArrayList<SearchSourceItem> ());
                    return;
                }

                boolean isContainChinese = false;
                if (SearchUtil.isContainChinese (keyword)) {
                    isContainChinese = true;
                }

                boolean matchPinyin= LocalSearchManager.instance ().isMatchPinyin ();

                boolean hasType = false;

                if (!SearchUtil.isEmpty (type)) {
                    hasType = true;
                }

                String word = "%" + keyword + "%";
                List<SearchSourceItem> list = new ArrayList<> ();
                if (isContainChinese || !matchPinyin) {
                    // 包含中文则

                    Where<SearchSourceItem> where = SQLite.select ().from (SearchSourceItem.class).where (
                            SearchSourceItem_Table.name.like (word)).and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation));
                    if (hasType) {
                        where = where.and (SearchSourceItem_Table.type.eq (type));
                    }
                    List<SearchSourceItem> sourceItems = where.orderBy (SearchSourceItem_Table.firstChars, true).queryList ();
                    list.addAll (sourceItems);
                } else {

                    Where<SearchSourceItem> where = null;
                    if (hasType) {
                        where = SQLite.select ().from (SearchSourceItem.class).where (
                                SearchSourceItem_Table.firstChars.like (word)).and (SearchSourceItem_Table.type.like (type))
                                .and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation))
                                .or (SearchSourceItem_Table.pinyins.like (word)).and (SearchSourceItem_Table.type.like (type))
                                .and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation))
                        ;

                    } else {
                        where = SQLite.select ().from (SearchSourceItem.class).where (
                                SearchSourceItem_Table.firstChars.like (word)
                        ).and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation)).
                                or (SearchSourceItem_Table.pinyins.like (word)).
                                and (SearchSourceItem_Table.entranceLocation.eq (entranceLocation));
                    }
                    List<SearchSourceItem> sourceItems = where.orderBy (SearchSourceItem_Table.firstChars, true).queryList ();
                    list.addAll (sourceItems);
                }

                List<SearchSourceItem> invalidateData = new ArrayList<> ();

                for (SearchSourceItem item : list) {
                    SearchSourceItem aa = SearchUtil.index (item, keyword);
                    if (!aa.hasIndex ()) {
                        //拼音匹配有点bug
                        invalidateData.add (item);
                    }
                }
                if (invalidateData.size () > 0) {
                    list.removeAll (invalidateData);
                }

                emitter.onSuccess (list);
            }
        });

    }
}
