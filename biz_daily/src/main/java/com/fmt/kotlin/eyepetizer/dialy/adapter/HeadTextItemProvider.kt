package com.fmt.kotlin.eyepetizer.dialy.adapter

import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.fmt.kotlin.eyepetizer.dialy.R
import com.fmt.kotlin.eyepetizer.dialy.databinding.DailyItemHeadTextBinding
import com.fmt.kotlin.eyepetizer.dialy.model.ProviderMultiModel

class HeadTextItemProvider : BaseItemProvider<ProviderMultiModel>() {

    override val itemViewType: Int
        get() = ProviderMultiModel.Type.TYPE_TITLE
    override val layoutId: Int
        get() = R.layout.daily_item_head_text

    override fun convert(helper: BaseViewHolder, item: ProviderMultiModel) {
        val baseDataBindingHolder = BaseDataBindingHolder<DailyItemHeadTextBinding>(helper.itemView)
        baseDataBindingHolder.dataBinding?.model = item
    }
}